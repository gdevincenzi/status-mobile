(ns status-im.test.init.core
  (:require [cljs.test :refer-macros [deftest is testing]]
            [status-im.init.core :as init]))

(deftest initialize-multiaccount-db
  (testing "it preserves universal-links/url"
    (is (= "some-url" (get-in (init/initialize-multiaccount-db
                               {:db {:universal-links/url "some-url"}}
                               "address")
                              [:db :universal-links/url])))))
