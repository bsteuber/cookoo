(ns cookoo.test.tools.args
  (:require-macros [cemerick.cljs.test :refer [deftest testing is]])
  (:require [cemerick.cljs.test :as t]
            [cookoo.tools.args :as a]))

(defn check [parser]
  #(is (= (parser %1) %2)))

(deftest optarg-test
  (let [ok? (check (a/optarg-parser string?))]        
    (ok? [] [nil nil])
    (ok? [1] [nil [1]])
    (ok? ["a"] ["a" nil])
    (ok? ["a" "b"] ["a" ["b"]]))

  (let [ok? (check (a/combine-parsers + 0 [(a/optarg-parser number?)
                                           (a/optarg-parser number?)]))]    

    (ok? [] [0 nil])
    (ok? [1] [1 nil])
    (ok? [1 2] [3 nil])
    (ok? [1 2 3] [3 [3]]))

  (let [ok? (check (a/combine-seq [(a/optarg-parser number?)
                                   (a/optarg-parser number?)]))]    
    (ok? [] [[nil nil] nil])
    (ok? [1] [[1 nil] nil])
    (ok? [1 2] [[1 2] nil])
    (ok? [1 2 3] [[1 2] [3]]))

  (let [ok? #(is (= (a/parse-optargs %1 [number? 0] [string? ""] map?)
                    %2))]
    (ok? [] [0 "" nil])
    (ok? [1 2] [1 "" nil 2])
    (ok? [1 "2" 3] [1 "2" nil 3])
    (ok? [1 "2" {}] [1 "2" {}]))

  (let [ok? #(is (= (a/parse-optargs %1 string? keyword?)
                    %2))]
    (ok? ["Foo" :Foo] ["Foo" :Foo] )
    (ok? ["Foo" nil] ["Foo" nil])))
 
