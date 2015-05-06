(ns ombs.view
  (:require 
    [net.cgrand.enlive-html :as h]
    [noir.session :as sess] 
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

(h/deftemplate index "ombs/index.html" [ctxt]
  [:#error] (fn [match] 
              (if-let [error (:error ctxt)]
                ((h/content error)      match)  
                ((h/content "") match) )))

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

(h/deftemplate user "ombs/user.html" [] 
  [:#user]  (h/content (sess/get :username "Anon"))
  )
