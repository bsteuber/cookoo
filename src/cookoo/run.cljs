(ns cookoo.run
  (:require [cookoo.db.access :as db]
            [cookoo.db.knowledge-base :as kb]
            [cookoo.db.schema :as s]
            [cookoo.db.validate :as v]
            [cookoo.tools.log :refer [log]]
            [cookoo.tools.validate :refer [pr-errors]]))

(defn main []
  (s/init-schema)
  (pr-errors (v/validate-block (kb/query-log))
             db/s))
 
