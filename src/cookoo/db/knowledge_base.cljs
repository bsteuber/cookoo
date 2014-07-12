(ns cookoo.db.knowledge-base
  (:require [cookoo.tools.coll :refer [as-seq conjv conjs]]
            [cookoo.tools.debug :refer [log]]))

(defn query [kb obj attr]
  (or (get-in kb [attr obj])
      #{}))

(defn fact [kb [obj attr val]]
  (update-in kb [attr obj]
             #(conj (or % #{}) 
                    val)))

(defn facts [kb facts]
  (reduce fact kb facts))
