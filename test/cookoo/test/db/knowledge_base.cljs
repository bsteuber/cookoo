(ns cookoo.test.db.knowledge-base
  (:require-macros [cemerick.cljs.test :refer [deftest testing is]])
  (:require [cemerick.cljs.test :as t]
            [cookoo.db.transactor :refer [query clear! fact! deny!]]))

(enable-console-print!)
