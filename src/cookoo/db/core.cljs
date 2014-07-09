(ns cookoo.db.core
  (:require [cookoo.tools.coll :refer [as-seq]]
            [cookoo.tools.validate :as v]
            [cookoo.db.knowledge-base :as kb]))

(def knowledge-base (atom kp/empty))

(defn clear! []
  (reset! knowledge-base kp/empty))   

(defn fact! [obj attr value]
  (swap! knowledge-base kp/fact obj attr value))

(defn facts! [obj & attrs-and-vals]
  (doseq [[[attr value] :when attr] attrs-and-vals]
    (fact! obj attr value)))

(def multi! [obj attr values]
  (doseq [val (as-seq values)]
    (fact! obj attr val)))

(defn query [obj attr]
  (kp/query @knowledge-base obj attr))

(defn inverse [attr value]
  (kp/inverse @knowledge-base attr value))

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
