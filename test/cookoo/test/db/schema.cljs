(ns cookoo.test.db.schema
  (:require-macros [cemerick.cljs.test :refer [deftest testing is]])
  (:require [cemerick.cljs.test :as t]
            [cookoo.db.knowledge-base :refer [query query-log clear! fact! deny!]]
            [cookoo.db.schema :refer [init-schema]]
            [cookoo.db.validate :refer [validate-block]]))

(deftest schema-test
  (testing "init schema"
    (clear!)
    (init-schema))
  (testing "object validator"
    (fact! :foo :validator list?)
    (is (set? (query :foo :validator))))
#_  (testing "validates"
    (is (empty? (validate-block (query-log))))))
