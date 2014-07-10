(ns cookoo.test.db.knowledge-base
  (:require-macros [cemerick.cljs.test :refer [deftest testing is]])
  (:require [cemerick.cljs.test :as t]
            [cookoo.db.knowledge-base :refer [query clear! fact! deny!]]))

(enable-console-print!)

(deftest test-knowledge-base
  (clear!)
  (testing "set cardinality"
    (fact! :foo :card :set)
    (fact! 1 :foo 2)
    (is (= #{2} (query 1 :foo)))
    (fact! 1 :foo 3)
    (is (= #{2 3} (query 1 :foo)))
    (deny! 1 :foo 2)
    (is (= #{3} (query 1 :foo))))
  (testing "list cardinality"
    (fact! :bar :card :list)
    (fact! 1 :bar 2)
    (is (vector? (query 1 :bar)))      
    (is (= [2] (query 1 :bar)))
    (fact! 1 :bar 3)
    (is (vector? (query 1 :bar)))      
    (is (= [2 3] (query 1 :bar)))
    (deny! 1 :bar 2)
    (is (vector? (query 1 :bar)))))
