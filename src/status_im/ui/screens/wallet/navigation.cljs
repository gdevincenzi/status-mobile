(ns status-im.ui.screens.wallet.navigation
  (:require [status-im.constants :as constants]
            [status-im.ui.screens.navigation :as navigation]
            [status-im.ui.screens.wallet.signing-phrase.views :as signing-phrase]))

(def transaction-send-default
  {:method constants/web3-send-transaction
   :symbol :ETH})

(defmethod navigation/preload-data! :wallet-stack
  [db [event]]
  (let [wallet-set-up-passed? (get-in db [:multiaccount :wallet-set-up-passed?])]
    (if (or (= event :navigate-back) wallet-set-up-passed?)
      db
      (assoc db :popover/popover {:view [signing-phrase/signing-phrase]}))))

(defmethod navigation/preload-data! :wallet-send-transaction-request
  [db [event]]
  (if (= event :navigate-back)
    db
    (-> db
        (assoc-in [:wallet :request-transaction] {:symbol :ETH})
        (assoc-in [:wallet :send-transaction] transaction-send-default))))

(defmethod navigation/preload-data! :wallet-send-transaction
  [db [event]]
  (if (= event :navigate-back)
    db
    (assoc-in db [:wallet :send-transaction] transaction-send-default)))

(defmethod navigation/preload-data! :wallet-add-custom-token
  [db [event]]
  (dissoc db :wallet/custom-token-screen))
