(ns ombs.core
  "Contains main logic. No validations. All function hope that you give to it valid data. Use in
  handlers, views, etc. "
  (:require [ombs.db.event :as dbe]
            [ombs.db.payment :as dbpay]
            [ombs.db.admin :as db-adm]
            [ombs.db.user :as dbu]
            [ombs.funcs :as fns]
            [ombs.validate :refer [add-error]]
            [noir.response :refer [redirect]]
            ))

