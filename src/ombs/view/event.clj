(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]  
    [ombs.core :as core]
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
  [:#error] (h/content (reduce str (map #(str "|" % "|") (vld/get-errors :event))))
  )

(def event-sel [:article])

(h/defsnippet event-elem "../resources/public/event.html" event-sel 
  [{{:keys [event price remain date]} :event 
    users :users 
    :as event-user-list}]
  [:#ename] (h/set-attr :value event)
  [:#eprice] (h/set-attr :value (str price))
  [:#edebt] (h/set-attr :value (core/debt (sess/get :username) event date))
  [:.action] (fn [match]
               (if (core/need-button? (sess/get :username) event-user-list)  ;if user participated in events
                 ((h/remove-attr :disabled "")  match)
                 ((h/set-attr :disabled "")     match)      
                 )))
