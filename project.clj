(defproject cookoo "1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojurescript "0.0-2261"]
                 [org.clojure/clojure "1.6.0"]
  		 [org.clojars.franks42/cljs-uuid-utils "1.0.0-SNAPSHOT"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.3.1"]]
  :cljsbuild  {:builds [{:source-paths ["src"],
                         :id "dev",
                         :compiler
                         {:pretty-print true,
                          :output-to "build/cookoo.js",
                          :optimizations :whitespace}}
                        {:source-paths ["src" "test"],
                         :id "test",
                         :compiler
                         {:pretty-print true,
                          :output-to "build/cookoo.js",
                          :optimizations :whitespace}}
                        {:source-paths ["src"],
                         :id "release",
                         :compiler
                         {:pretty-print true,
                          :output-to "build/cookoo.js",
                          :optimizations :advanced}}]
               :test-commands {"unit" ["phantomjs" 
                                       :runner
                                       "build/cookoo_test.js"]}})
