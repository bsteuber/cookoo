(ns cookoo.db.core)

(def db-atom (atom []))

(def fact! [obj attr value]
  (swap! db-atom conj [obj attr value]))

(defn facts! [obj & attrs-and-vals]
  (doseq [[[attr value] :when attr] attrs-and-vals]
    (fact! obj attr value)))

(defn query [obj attr])

(defn inverse [attr val])

(defn attr! [id name clazz & {:keys [card default validate]}]
  (facts! id
    [:class :Attr]
    [:name name]
    [:attr-class clazz]
    [:card card]
    [:default default]
    (when validator
      [:validator validator]))))

(defn inst! [id name clazz & attrs-and-vals]
  (apply facts! id
    [:name name]
    [:class clazz]
    attrs-and-vals))

(defn class! [id name & [super attrs & {:keys [validator]}]]
  (inst! id name :Class
     [:super super]
     [:validator validator]
     [:has-attr attrs]))

(defn enum! [id name & values-and-names]
   (class! id name :Enum)
   (fact! id :validator (->> values-and-names
   	     		     (map first)
			     (apply hash-set)))
   (doseq [[v name] values-and-names]
     (inst! v name id)))
