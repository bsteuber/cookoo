(ns cookoo.tools.debug)

(extend-type js/Function
  IPrintWithWriter
  (-pr-writer [a writer opts]
    (-write writer "#<Fn>")))

(defn s [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn fail [& objs]
  (throw (js/Error. (apply s objs))))

(defn log [& objs]
  (.log js/console (apply s objs))
  (first objs))
