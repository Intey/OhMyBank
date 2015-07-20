(ns ombs.view.event
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.validation :as vld]  
    ))

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
