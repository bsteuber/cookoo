(ns cookoo.db.validate
  (:require [cookoo.tools.validate :refer [validator validate run wrap]]
            [cookoo.db.access :as db]
            [cookoo.db.transactor :as kb]))

(defn attr-v [attr]
  (wrap (db/attr-validators attr)       
        (db/s "invalid value for attribute" attr)))

(defn class-v [clazz]
  (let [class-val (db/class-validators clazz)
        required-attrs (db/class-required-attrs clazz)
        required-val (map (fn [attr]
                            (validator                    
                              (fn [obj]
                                (db/attr-exists? obj attr))
                              (db/s "missing required attribute" attr)))
                          required-attrs)]
    (wrap [class-val
           required-val]
          (db/s "invalid object for class" clazz))))

(def is-attr-v
  (validator db/attr? "attribute expected"))

(defn line-v [[method obj attr value]]
  (run is-attr-v attr)
  (run (class-v (db/obj-class obj))
       obj)
  (let [actual-value (kb/query obj attr)]
    (run (attr-v attr)
         actual-value)))

(defn block-validator [block]
  (doseq [line block]
    (run line-v line)))
        
(defn validate-block [block]
  (validate block-validator block))
