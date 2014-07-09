(ns cookoo.db.access
  (:require [clojure.set :refer [union]]
            [clojure.string :as str]
            [cookoo.db.knowledge-base :refer [query fact?]]))

(defn hull [f start-set]
  (let [next-set (->> start-set 
                      (map f)
                      (apply hash-set)
                      (union start-set))]
    (if (= start-set next-set)
      start-set
      (recur f next-set))))

(defn obj-name [x]
  (query x :name))

(defn s [& args]
  (->> args
       (map #(or (obj-name %)
                 (str %)))
       (str/join " ")))

(defn obj-class [x]
  (query x :class))

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

(defn instance? [obj clazz]
  ((supers (obj-class obj))
   clazz))

(defn object? [obj]
  (instance? obj :Object))

(defn attr? [attr]
  (instance? attr :Attr))

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
  [(query attr :validator)
   (class-validators (attr-class attr))])


