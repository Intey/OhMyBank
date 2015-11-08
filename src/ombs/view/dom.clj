(ns ombs.view.dom
  (:require [net.cgrand.enlive-html :as h])) 

(defn content-wrap [value match] ((h/content (str value)) match))

(defn set-attr-class
  ([attr]
    (set-attr-class attr ""))
  ([attr value]
    {:pre (= (type attr) java.lang.String)}
    (comp (h/set-attr attr value) (h/add-class attr))) )

(defn rm-attr-class [attr]
  {:pre (= (type attr) java.lang.String)}
  (comp (h/remove-attr attr ) (h/remove-class attr)) )
