(ns status-im2.contexts.chat.home.view
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [i18n.i18n :as i18n]
            [react-native.core :as rn]
            [quo2.core :as quo]
            [utils.re-frame :as rf]
            [status-im2.common.home.view :as common.home]
            [status-im2.contexts.chat.home.contact-request.view :as contact-request]
            [status-im2.contexts.chat.home.chat-list-item.view :as chat-list-item]
            [status-im.ui2.screens.common.contact-list.view :as contact-list]))

(defn get-item-layout [_ index]
  #js {:length 64 :offset (* 64 index) :index index})

(defn filter-items-by-tab [tab items]
  (if (= tab :groups)
    (filter :group-chat items)
    (filter :chat-id items)))

(defn welcome-blank-chats []
  [rn/view {:style {:flex 1 :align-items :center :justify-content :center}}
   [quo/icon :i/placeholder]
   [quo/text {:weight :semi-bold} (i18n/label :t/no-messages)]
   [quo/text (i18n/label :t/blank-messages-text)]])

(defn chats [selected-tab]
  (let [{:keys [items search-filter]} (rf/sub [:home-items])
        items (filter-items-by-tab selected-tab items)]
    (if (and (empty? items)
             (empty? search-filter))
      [welcome-blank-chats]
      [rn/flat-list
       {:key-fn                       #(or (:chat-id %) (:public-key %) (:id %))
        :get-item-layout              get-item-layout
        :on-end-reached               #(re-frame/dispatch [:chat.ui/show-more-chats])
        :keyboard-should-persist-taps :always
        :data                         items
        :render-fn                    chat-list-item/chat-list-item}])))

(defn welcome-blank-contacts []
  [rn/view {:style {:flex 1 :align-items :center :justify-content :center}}
   [quo/icon :i/placeholder]
   [quo/text {:weight :semi-bold} (i18n/label :t/no-contacts)]
   [quo/text (i18n/label :t/blank-contacts-text)]])

(defn contacts [contact-requests]
  (let [items (rf/sub [:contacts/active-sections])]
    (if (empty? items)
      [welcome-blank-contacts]
      [:<>
       (when (pos? (count contact-requests))
         [contact-request/contact-requests contact-requests])
       [contact-list/contact-list {:icon :options}]])))

(defn tabs []
  (let [selected-tab (reagent/atom :recent)]
    (fn []
      (let [contact-requests (rf/sub [:activity-center/pending-contact-requests])]
        [:<>
         [quo/discover-card {:title       (i18n/label :t/invite-friends-to-status)
                             :description (i18n/label :t/share-invite-link)}]
         [quo/tabs {:style          {:margin-left   20
                                     :margin-bottom 20
                                     :margin-top    24}
                    :size           32
                    :on-change      #(reset! selected-tab %)
                    :default-active @selected-tab
                    :data           [{:id    :recent
                                      :label (i18n/label :t/recent)}
                                     {:id    :groups
                                      :label (i18n/label :t/groups)}
                                     {:id                :contacts
                                      :label             (i18n/label :t/contacts)
                                      :notification-dot? (pos? (count contact-requests))}]}]
         (if (= @selected-tab :contacts)
           [contacts contact-requests]
           [chats @selected-tab])]))))

(defn home []
  [:<>
   [common.home/top-nav {:type :default}]
   [common.home/title-column {:label               (i18n/label :t/messages)
                              :handler             #(rf/dispatch [:bottom-sheet/show-sheet :add-new {}])
                              :accessibility-label :new-chat-button}]
   [tabs]])
