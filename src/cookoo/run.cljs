(ns cookoo.run
  (:require [cookoo.db.knowledge-base :as kb]
            [cookoo.db.schema :as s]
            [cookoo.db.validate :as v]
            [cookoo.tools.validate :refer [pr-errors]]))

(defn main []
  (s/init-schema)
  (pr-errors (v/validate-block (kb/query-log))))

