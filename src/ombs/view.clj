(ns ombs.view
  (:require 
    [net.cgrand.enlive-html :as h]
    ;[noir.validation :as valids] 
    ;[noir.util.anti-forgery :refer [anti-forgery-field]] ; security - need, add field fo srcf defence

    ) )

(h/deftemplate index "ombs/index.html" [ctxt]
               [:#params] (h/content (str ctxt))
               )

;"Generate register page. If in given params founded keys for this page - fill fields with 
;founded values"
(h/deftemplate register "ombs/register.html" [params] 
               [:#username]     (h/set-attr :value (params "username"))
               [:#birthdate]    (h/set-attr :value (params "birthdate"))
               [:#student-flag] (if (not-empty (params "student-flag")) 
                                  (h/set-attr :checked "on")  ; check
                                  (h/set-attr "" "") )        ; unckeck
               )

(h/deftemplate user "ombs/user.html" [params] 
               [:#error] (h/content (if (:error params) (:error params) "" ))
               [:#user]  (h/content (:username params))
               )
