(ns cookoo.db.core
  (:require [cookoo.tools.validate :as v]))

(def db-atom (atom []))

(def fact! [obj attr value]
  (swap! db-atom conj [obj attr value]))

(defn facts! [obj & attrs-and-vals]
  (doseq [[[attr value] :when attr] attrs-and-vals]
    (fact! obj attr value)))

(defn query [obj attr])

(defn inverse [attr val])

(defn validator! [id [pred msg]]
  (when (and pred msg)
    (fact! id :validator (v/validator pred msg))))

(defn attr! [id name clazz & {:keys [card default validator]}]
  (facts! id
    [:class :Attr]
    [:name name]
    [:attr-class clazz]
    [:card card]
    [:default default])
  (validator! id validator))

(defn inst! [id name clazz & attrs-and-vals]
  (apply facts! id
    [:name name]
    [:class clazz]
    attrs-and-vals))

(defn class! [id name & [super attrs & {:keys [validator]}]]
  (inst! id name :Class
     [:super super]
     [:has-attr attrs])
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
