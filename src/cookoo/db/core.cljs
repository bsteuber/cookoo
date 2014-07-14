(ns cookoo.db.core
  (:require [cookoo.tools.args :refer [parse-optargs]]
            [cookoo.tools.coll :refer [as-seq]]
            [cookoo.tools.debug :refer [fail log]]
            [cookoo.tools.validate :as v]
            [cookoo.db.transactor :as db]
            [cookoo.db.indexers :as idx]
            [cookoo.db.uid :refer [id? fresh-uid]]))

(defn fact! [o a v]
  (db/fact! [o a v]))

(defn check-facts [s facts]
  (doseq [f (remove nil? facts)]
    (when-not (and (seqable? f)
                   (= (count f) 2))
      (fail "Illegal fact in" s f))))

(defn block! [block]
  (doseq [line block]
    (apply fact! line)))

(defn facts! [obj & facts]
  #_(log "(facts!)" obj facts)
  (check-facts "facts!" facts)
  (->> facts
       (remove nil?)   
       (map (partial cons obj))
       block!)
  obj)

(defn apply! [obj & args]
  #_(log "(apply!)" obj args)
  (apply apply facts! obj args))

(defn multi! [obj attr values]
  (doseq [val (as-seq values)]    
    (fact! obj attr val)))

(defn call! [fn-id & args]
  (let [f (query fn-id :call)]
    ;;todo check argspec
    (apply f args)))

(defn new! [class-id obj]
  (let [id (fresh-uid)
        constructor (query class-id :constructor)]
    
    (call! constructor id obj)))




(defn object! [id & args]
  (let [[title clazz & facts :as p] (parse-optargs args string? id?)]   
    #_(log "(object!)" title clazz facts)
    (check-facts "object!" facts)
    (apply! id
            (when clazz
              [:class clazz])
            (when title
              [:title title])
            facts)))

(defn validator! [id title pred]
  (object! id title :Validator
           [:pred pred]))

(defn trigger! [id target-attr indexer]
  (object! :id :Trigger
           [:target-attr target-attr]
           [:indexer indexer]))

(defn attr! [owner-class id title attr-class & args]
  (let [[options & facts] (parse-optargs args map?)        
        {:keys [index]} options
        clazz (if index :IndexAttr :DbAttr)]    
    #_(log "(attr!)" owner-class id title)
    (check-facts "attr!" facts)
    (apply object! id title clazz
           [:owner-class owner-class]
           [:attr-class attr-class]
           facts)
    (when-let [[on-attr f] (as-seq index)]
      (fact! id :card :set)
      (trigger! on-attr id (or f idx/inverse))))
  id)

(defn seq-of? [pred coll]
  #_(log "(seq-of?)" coll (sequential? coll))
  (and (sequential? coll)
       (every? pred coll)))

(defn attr-specs? [specs]
  (seq-of? sequential? specs))

(defn parents? [parents]
  (or (id? parents)
      (seq-of? id? parents)))

(defn child! [])

(defn class! [id title & args]  
  (let [[parent meta options attr-specs & facts :as parsed]  
        (parse-optargs args parents? id? map? attr-specs?)]
    #_(log "(class!)" id parent meta title facts)
    (check-facts "class!" facts)    
    (object! id title meta)
    (multi! id :parent parent)
    (doseq [attr-spec attr-specs]
      (apply attr! id attr-spec))
    (apply! id facts)))

(defn enum! [id title & values-and-titles]
   (let [valid? (->> values-and-titles
 	             (map first)
		     (apply hash-set))
         msg (str title " expected")]
     (doseq [[v title] values-and-titles]
       (object! v title id))
     (class! id title :Enum []
             [:validator (v/validator valid? msg)])))
