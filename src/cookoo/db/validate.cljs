(ns cookoo.db.validate
  (:require [cookoo.tools.validate :refer [validator validate]]
            [cookoo.db.access :as db]))

(defn id? [x]
  (or (keyword? x)
      (instance UUID x)))

(defn attr-validator [attr]
  (let [attr-val (db/attr-validators attr)      
        clazz  (db/attr-class attr)]
    [(db/class-validators clazz) attr-val]))

(defn class-validator [clazz]
  (let [class-val (db/class-validators clazz)
        required-attrs (db/class-required-attrs clazz)
        required-val (map (fn [attr]
                            (validator                    
                              (fn [obj]
                                (db/attr-exists? obj attr))
                              (str "missing required attribute: " (db/name attr)))
                          required-attrs)]
    [class-val
     required-val]))
        
(defn validate-transaction [block])