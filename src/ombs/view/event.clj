(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]  
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    [clj-time.format :refer [formatter unparse]]
    [clj-time.local :refer [local-now]]
    [clj-time.core :refer [date-time]]
    ))

; Helpers, for creation list of user for participation
(def usercheckbox-sel [:.users] )
(h/defsnippet usercheckbox-elem "../resources/public/addevent.html" usercheckbox-sel [{username :name}] 
  [:.userbox] (h/do-> 
                (h/content username)  
                (h/set-attr :value username)))

(h/deftemplate addevent-page "../resources/public/addevent.html"
  [users]
  [:.users] (h/content ( map #(usercheckbox-elem %) users) )
  [:#edate] (h/set-attr :value (unparse (formatter "YYYY-MM-dd") (local-now) ) )
  [:#error] (h/content (errors-string :event))
  )

(def event-sel [:.event])

(h/defsnippet event-elem "../resources/public/event.html" event-sel 
  [{:keys [name price date author]}]
  [:.name]   (h/set-attr :value name)
  [:.date]   (h/set-attr :value date)
  [:.author] (h/set-attr :value author)
  [:.price]  (h/set-attr :value (str price))
  [:.debt]   (h/set-attr :value (core/debt (sess/get :username) name date))
  [:.action.participate] (fn [match]
               (if (core/need-button? (sess/get :username) name date)  ;if user participated in name
                 ((h/remove-attr :disabled "")  match)
                 ((h/set-attr :disabled "")     match)))
  [:.action.pay] (fn [match]
                   (if (and 
                         (not= (core/debt (sess/get :username) name date) 0.0) ; have debt
                         (not (core/need-button? (sess/get :username) name date)) ;i'm stake in name
                         )
                     ((h/remove-attr :disabled "")  match)
                     ((h/set-attr :disabled "")     match)))
  [:.action.start] (fn [match]
                     ;(println (str "author: " author " name "name " date " date " initial? " (core/is-initial? name date) ))
                     (if (and (= author (sess/get :username)) (core/is-initial? name date))
                       ((h/remove-attr :disabled "")  match)
                       ((h/set-attr :disabled "")     match)))
  
  )
