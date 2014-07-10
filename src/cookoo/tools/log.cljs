(ns cookoo.tools.log)

(defn log [& objs]
  (.log js/console (pr-str-with-opts objs (assoc (pr-opts) :readably false))))
