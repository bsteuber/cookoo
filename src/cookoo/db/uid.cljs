(ns cookoo.db.uid
  (:require [cljs-uuid-utils :refer [make-random-uuid uuid-string]]))

(def fresh-uid
 (comp uuid-string make-random-uuid))

(defn fresh-uids []
  (repeatedly fresh-uid))
