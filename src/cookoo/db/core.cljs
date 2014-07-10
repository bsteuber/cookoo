(ns cookoo.db.core
  (:require [cookoo.tools.coll :refer [as-seq]]
            [cookoo.tools.log :refer [log]]
            [cookoo.tools.validate :as v]
            [cookoo.db.knowledge-base :refer [fact! facts! deny! multi! query]]))

(defn object! [id clazz & facts]
  (apply! facts! id
          [:class clazz]
          facts))

(defn named! [id clazz name & args]
  (apply! object! clazz
          [:name name]          
          facts))

(defn validator! [id msg pred]
  (object! id :Validator
           [:message msg]
           [:pred pred]))

(defn trigger! [id target-attr indexer]
  (object! :id :Trigger
           )
  (fact! attr :trigger trigger))

(defn ->trigger [f generated-attr]
  (fn [obj val]
    (for [[os v] (partition 2 (f obj val))
          o (as-seq os)]
      [o generated-attr v])))            

(defn attr! [owner-class id name attr-class & options-and-facts]
  (let [[options & facts] (if (map? (first options-and-facts)
                                    options-and-facts
                                    (cons {} options-and-facts)))
        {:keys [index]} options
        clazz (if index :IndexAttr :DbAttr)]            
    (named! id name clazz
           [:owner-class owner-class]
           [:attr-class attr-class]
           facts)
    (when-let [[on-attr f] index]
      (fact! id :card :set)
      (index! on-attr (->indexer f id)))))

(defn class! [id name & [super attrs & facts]]  
  (named! id name :Class)
  (multi! id :super super)
  (doseq [attr-spec attrs]
    (apply attr! id attr-spec))
  (apply! id facts))

(defn enum! [id name & values-and-names]
   (let [valid? (->> values-and-names
 	             (map first)
		     (apply hash-set))
         msg (str name " expected")]
     (class! id name :Enum :validator [valid? msg]))
   (doseq [[v name] values-and-names]
     (named! v name id)))
