(ns cookoo.db.access
  (:require [clojure.set :refer [union]]
  	    [cookoo.db.core :as raw]))

(defn hull [f start-set]
  (let [next-set (->> start-set 
       		      (map f)
		      (apply hash-set)
		      (union start-set))]
    (if (= start-set next-set)
      start-set
      (recur f next-set))))

(defn query [obj attr]
  (let [result  (raw/query obj attr)
        default (raw/query attr :default)]
    (or result default)))

(defn attr-class [attr]
  (query attr :attr-class))

(defn attr-card [attr]
  (query attr :card))

(defn attr-exists? [obj attr]
  (not (nil? (query obj attr))))

(defn super [clazz]
  (query clazz :super))

(defn supers [clazz]
  (hull super #{clazz}))

(defn super-union [clazz attr]
  (->> clazz
       supers
       (map #(query % attr))
       (apply union)))

(defn class-attrs [clazz]
  (super-union clazz :has-attr))

(defn class-validators [clazz]
  (super-union clazz :validator))

(defn class-required-attrs [clazz]
  (->> (class-attrs clazz)
       (filter #(= (attr-card %) :single))
       (remove #(attr-exists? % :default))))

(defn attr-validators [attr]
  (union (query attr :validator)
  	 (class-validators (attr-class attr))))


