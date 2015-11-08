(ns ombs.view.actions
  (:require
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [ombs.core :as core]
    )
  )

(h/defsnippet parts-snip "../resources/public/parts.html" [:#parts-row] [parts]
  [:.parts] (h/set-attr :value (str parts)))
(defn fill-parts [parts match]
  (if (= 0 parts)
    nil
    ((h/content (parts-snip parts)) match)))

(defn pay [name date match]
  (if (and
        (core/participated? (sess/get :username) name date)
        (core/is-active? name date) ) ; in-progress
    ; add parts field
    ((h/content "Pay") ((rm-attr-class "disabled")   match))
    ((h/content "Pay") ((set-attr-class "disabled")  match))
    ))

(defn start [name date author match]
  (if (and (= author (sess/get :username))
           (core/is-initial? name date)
           (> (core/participants-count name date) 0))
    ((h/remove-class "disabled") match)
    ((h/add-class "disabled")    match)))

(defn participate [name date status match]
  (if (and
        (not (core/participated? (sess/get :username) name date))
        (not= status "finished"))
    ((h/remove-attr "disabled") match)
    ((h/add-class "disabled")   match)))

