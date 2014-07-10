(ns cookoo.db.core
  (:require [cookoo.tools.coll :refer [as-seq]]
            [cookoo.tools.log :refer [log]]
            [cookoo.tools.validate :as v]
            [cookoo.db.knowledge-base :refer [fact! deny! query]]))

(defn facts! [obj & attrs-and-vals]
  (doseq [[attr value] (partition 2 attrs-and-vals)]
    (when attr
      (fact! obj attr value))))

(defn multi! [obj attr values]
  (doseq [val (as-seq values)]
    (fact! obj attr val)))

(defn validator! [id [pred msg]]
  (when (and pred msg)
    (fact! id :validator (v/validator pred msg))))

(defn iattr! [id name clazz attr-class validator]
  (facts! id
    :class clazz
    :name name
    :attr-class attr-class)
  (validator! id validator))

(defn attr! [id name attr-class & {:keys [card default validator]}]
  (iattr! id name :DbAttr attr-class validator)
  (facts! id
    :card card
    :default default))

(defn index! [attr index-fn]
  (fact! attr :index index-fn))

(defn ->index-fn [f generated-attr]
  (fn [obj val]
    (for [[os v] (partition 2 (f obj val))
          o (as-seq os)]
      [o generated-attr v])))            

(defn index-attr! [id name attr-class on-attr f & {:keys [validator]}]
  (iattr! id name :IndexAttr attr-class validator)
  (index! on-attr (->index-fn f id)))

(defn inst! [id name clazz & attrs-and-vals]
  (apply facts! id
    :name name
    :class clazz
    attrs-and-vals))

(defn class! [id name & [super attrs & {:keys [validator]}]]
  (inst! id name :Class)
  (multi! id :super super)
  (multi! id :has-attr attrs)  
  (validator! id validator))

(defn enum! [id name & values-and-names]
   (class! id name :Enum)
   (let [valid? (->> values-and-names
 	             (map first)
		     (apply hash-set))
         msg (str name " expected")]
     (validator! id [valid? msg]))
   (doseq [[v name] values-and-names]
     (inst! v name id)))
