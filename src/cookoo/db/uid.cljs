(ns cookoo.db.uid
  (:require [cljs-uuid-utils :refer [make-random-uuid uuid-string]]))

(defn id? [x]
  (or (keyword? x)
      (instance? UUID x)))

(def fresh-uid
 (comp uuid-string make-random-uuid))

(defn fresh-uids []
  (repeatedly fresh-uid))
