(ns ombs.view
  (:require [net.cgrand.enlive-html :as h]))

(h/deftemplate index "ombs/index.html" [ctxt]
               [:title]  (h/content "Awesome application")
               [:#error] (h/set-attr "style" (str "display:" (if (:error ctxt) "inline" "none")))
               [:#error] (h/content (if (:error ctxt) (:error ctxt) "" )))

(h/deftemplate register "ombs/register.html" [ctxt]
               [:#error] (h/content (if (:error ctxt) (:error ctxt) "" ))
               )

(h/deftemplate user "ombs/user.html" [ctxt] 
               [:#error] (h/content (if (:error ctxt) (:error ctxt) "" ))
               [:#user]  (h/content (:username ctxt))
               )
