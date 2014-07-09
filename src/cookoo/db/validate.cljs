(ns cookoo.db.validate
  (:require [cookoo.db.access :as db])

(defn id? [x]
  (or (keyword? x)
      (instance UUID x)))

(defn validate [validators x]
  ((apply every-pred (flatten validators))
   x))

(defn attr-validator [attr]
  (let [attr-val (db/attr-validators attr)	
        clazz  (db/attr-class attr)]
    [attr-val (db/class-validators clazz)]))

(defn class-validator [clazz]
  (let [class-val (db/class-validators clazz)
        required-attrs (db/class-required-attrs clazz)
	required-val (map (fn [attr]
		        (fn [obj]
		          (db/attr-exists? obj attr)))
		      required-attrs)]
    [class-val
     required-val]))
        
(defn validate-transaction [block])