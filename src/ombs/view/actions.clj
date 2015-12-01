(ns ombs.view.actions
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [ombs.core :as core]
    [ombs.view.dom :as dom]))

(h/defsnippet parts-snip "../resources/public/parts.html" [:#parts-row] [parts]
  [:.parts] (h/set-attr :value (str parts)))

(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))

(defn pay [match]
  ((comp (h/set-attr :onclick "pay(this)") (h/content "Pay")) match))

(defn start [match]
  ((comp (h/set-attr :onclick "start(this)") (h/content "Start")) match))

(defn participate [match]
  ((comp (h/set-attr :onclick "participate(this)") (h/content "Participate")) match))

