(ns ombs.view
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess] 
    [noir.validation :as vld]
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

(def event-sel [:#event])

(h/defsnippet event-elem "ombs/user.html" event-sel [{:keys [name remain price]}]
  [:#e-name] (h/content name )
  [:#e-cost] (h/content (str price))
  [:#e-remain] (h/content (str remain))
  ;[:#e-debt] (h/content d)
  )

(h/deftemplate index "ombs/index.html" [ctxt]
  ;[:#ename] (fn [match] 
  ;  (vld/on-error :ename ((h/set-attr :placeholder (vld/get-errors :ename)) match) ))
  [:#error] (h/content (reduce str (map #(str "|" % "|") (vld/get-errors))))
                ;(if-let [error (:error ctxt)]
                ;     ((h/content error) match)  
                ;     ((h/content "") match) ) 
  ; hide log and reg forms, show logout form if have username in session 
  ;[:#logform] (hide)
  ;[:#regform] (hide)
  ;[:#logout]  (unhide)
  [:#event-list] (h/content (map #(event-elem %) (:events ctxt))) ) 

;"Generate register page. If in given params founded keys for this page - fill fields with 
;founded values"
(h/deftemplate register "ombs/register.html" [params] 
  [:#uname] (h/content (sess/get :username "no user"))
  [:#error] (h/content (str params))
  [:#username]     (h/set-attr :value (params "username"))
  [:#birthdate]    (h/set-attr :value (params "birthdate"))
  [:#student-flag] (if (not-empty (params "student-flag")) 
                     (h/set-attr :checked "on")  ; check
                     (h/set-attr "" "") )        ; unckeck
  )

(h/deftemplate user "ombs/user.html" 
  [event-list] 
  [:#user]  (h/content (sess/get :username "Anon"))
  [:#event-list] (h/content (map #(event-elem %) event-list))
  )
