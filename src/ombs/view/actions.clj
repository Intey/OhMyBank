(ns ombs.view.actions
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [ombs.core :as core]
    [ombs.view.dom :as dom]
    )
  )

(h/defsnippet parts-snip "../resources/public/parts.html" [:#parts-row] [parts]
  [:.parts] (h/set-attr :value (str parts)))
(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))

(defn pay [match] ((h/content "Pay") match))
(defn start [match] ((h/content "Start") match))
(defn participate [match] ((h/content "Participate") match))

