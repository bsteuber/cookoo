(ns cookoo.tools.debug)

(defn s [& objs]
  (pr-str-with-opts objs (assoc (pr-opts) :readably false)))

(defn fail [& objs]
  (throw (js/Error. (apply s objs))))

(defn log [& objs]
  (.log js/console (apply s objs))
  (first objs))
