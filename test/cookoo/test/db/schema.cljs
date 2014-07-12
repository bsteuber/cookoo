(ns cookoo.test.db.schema
  (:require-macros [cemerick.cljs.test :refer [deftest testing is]])
  (:require [cemerick.cljs.test :as t]
            [cookoo.db.core :refer [fact!]]
            [cookoo.db.transactor :refer [query clear! deny! save load! commit!]]
            [cookoo.db.schema :refer [init-schema]]
            [cookoo.db.validate :refer [validate-block]]
            [cookoo.tools.debug :refer [fail log]]))

(deftest schema-test  
  (testing "init schema"
    (clear!)
    (init-schema)
    (commit!))
  (testing "object validator"
      (let [vs (query :str-v :pred)
            v (first vs)]   
        (is (set? vs))
        (is (v "foo"))
        (is (not (v 42))))))
