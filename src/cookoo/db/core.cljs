(ns cookoo.db.core
  (:require [cookoo.tools.coll :refer [as-seq]]
            [cookoo.tools.log :refer [log]]
            [cookoo.tools.validate :as v]
            [cookoo.db.knowledge-base :as kb]
            [cookoo.db.indexers :as idx]))

(def query kb/query)
(def fact! kb/fact!)
(def deny! kb/deny!)

(defn block! [block]
  (doseq [[o a v] block]
    (fact! o a v)))

(defn facts! [obj & attr-val-pairs]
  (->> attr-val-pairs
       (map (partial cons obj))            
       block!))

(defn apply! [obj & args]
  (apply apply facts! args))

(defn multi! [obj attr values]
  (doseq [val (as-seq values)]
    (fact! obj attr val)))

(defn object! [id clazz & facts]
  (apply! facts! id
          [:class clazz]
          facts)
  id)

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
           [:target-attr target-attr]
           [:indexer indexer]))

(defn attr! [owner-class id name attr-class & options-and-facts]
  (let [[options & facts] (if (map? (first options-and-facts)
                                    options-and-facts
                                    (cons {} options-and-facts)))
        {:keys [index]} options
        clazz (if index :IndexAttr :DbAttr)]            
    (named! id clazz name
           [:owner-class owner-class]
           [:attr-class attr-class]
           facts)
    (when-let [[on-attr f] (as-seq index)]
      (fact! id :card :set)
      (trigger! on-attr id (or f idx/inverse))))
  id)

(defn class! [id name & [super attrs & facts]]  
  (named! id :Class name)
  (multi! id :super super)
  (doseq [attr-spec attrs]
    (apply attr! id attr-spec))
  (apply! id facts)
  id)

(defn enum! [id name & values-and-names]
   (let [valid? (->> values-and-names
 	             (map first)
		     (apply hash-set))
         msg (str name " expected")]
     (class! id name :Enum []
             [:validator (v/validator valid? msg)]))
   (doseq [[v name] values-and-names]
     (named! v id name)))
