// Form Validation Rules
import { defineRule } from "vee-validate";
import {
  required,
  email,
  alpha_spaces,
  alpha_num,
  numeric,
  confirmed,
} from "@vee-validate/rules";

// Presets
defineRule("required", required);
defineRule("email", email);
defineRule("alpha_spaces", alpha_spaces);
defineRule("numeric", numeric);
defineRule("alpha_num", alpha_num);
defineRule("confirmed", confirmed);

// Custom rules
defineRule("min_max", (value, [min, max]) => {
  if (value.length < min) {
    return `Feltet kan minimum ha ${min} karakterer.`;
  } else if (value.length > max) {
    return `Feltet kan maksimalt ha ${max} karakterer.`;
  }
  return true;
});

// Font Awesome Icons
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faRightFromBracket,
  faAngleDown,
  faPlus,
  faBell,
  faCircleUser,
  faCheck,
  faXmark,
  faAngleRight,
  faAngleLeft,
  faArrowRightArrowLeft,
  faMagnifyingGlass,
  faTrashCan,
  faBuilding,
  faLocationDot,
  faScrewdriverWrench,
  faIndustry,
  faCameraRetro,
  faKitchenSet,
  faTruckField,
  faDesktop,
  faChargingStation,
  faUserClock,
  faCouch,
  faHouseFloodWater,
  faImage,
} from "@fortawesome/free-solid-svg-icons";

// Add icons to library
library.add(
  faRightFromBracket,
  faAngleDown,
  faPlus,
  faBell,
  faCircleUser,
  faCheck,
  faXmark,
  faAngleRight,
  faAngleLeft,
  faArrowRightArrowLeft,
  faMagnifyingGlass,
  faTrashCan,
  faBuilding,
  faLocationDot,
  faScrewdriverWrench,
  faIndustry,
  faCameraRetro,
  faKitchenSet,
  faTruckField,
  faDesktop,
  faChargingStation,
  faUserClock,
  faCouch,
  faHouseFloodWater,
  faImage
);

import { SetupCalendar, Calendar, DatePicker } from "v-calendar";
import "v-calendar/dist/style.css";
import VueElementLoading from "vue-element-loading";
import Notifications from "@kyvg/vue3-notification";
import VueEasyLightbox from "vue-easy-lightbox";

// Create app
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";

createApp(App)
  .use(store)
  .use(router)
  .use(SetupCalendar, {})
  .use(Notifications)
  .component("Calendar", Calendar)
  .component("DatePicker", DatePicker)
  .component("font-awesome-icon", FontAwesomeIcon)
  .component("VueElementLoading", VueElementLoading)
  .component(VueEasyLightbox.name, VueEasyLightbox)
  .mount("#app");
