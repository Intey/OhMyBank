(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess]
    [noir.validation :as vld]  
    [ombs.core :as core]
    [ombs.validate :refer [errors-string]]
    ))



(def parts-row-sel [:#parts-row])
(h/defsnippet parts-snip "../resources/public/parts.html" parts-row-sel [parts] 
  [:.parts] (h/set-attr :value (str parts)))

(def event-sel [:.event])
(h/defsnippet event-elem "../resources/public/event.html" event-sel [{:keys [name price date author status parts]}]
  [:.name]   (h/set-attr :value name)
  [:.date]   (h/set-attr :value date)
  [:.author] (h/set-attr :value author)
  [:.price]  (h/set-attr :value (str price))
  [:.debt]   (h/set-attr :value (core/debt (sess/get :username) name date))
  [:#parts-row]  (fn [match]
                   (if  (= 0 parts) 
                     ((h/set-attr :hidden "")  match)
                     ((h/content (parts-snip parts)) match)))
  [:.action.participate] (fn [match]
               (if (and 
                     (not (core/participated? (sess/get :username) name date))
                     (not= status "finished") ; not finished
                     )  ;if user participated in name
                 ((h/remove-attr :disabled "")  match)
                 ((h/set-attr :disabled "")     match)))

  [:.action.pay] (fn [match]
                   (if (and 
                         (core/participated? (sess/get :username) name date)
                         (not= (core/debt (sess/get :username) name date) 0.0)) ; have debt
                     ((h/remove-attr :disabled "")  match)
                     ((h/set-attr :disabled "")     match))) 

  [:.action.start] (fn [match]
                     ;(println (str "author: " author " name "name " date " date " initial? " (core/is-initial? name date) ))
                     (if (and (= author (sess/get :username)) 
                              (core/is-initial? name date)
                              (> (core/participants-count name date) 0)
                              )
                       ((h/remove-attr :disabled "")  match)
                       ((h/set-attr :disabled "")     match)))
  
  )
