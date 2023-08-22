<template>
  <div
    id="nav-np"
    tabindex="0"
    @focusin="openNotifications"
    @focusout="closeNotifications"
  >
    <div id="notification-button-container">
      <NavButton
        @click="openNotificationsIfClosed"
        id="notification-button"
        icon="bell"
        tooltip-text="Notifikasjoner"
      />
      <div v-show="newNotifications > 0" id="notification-dot">
        <p>{{ newNotifications }}</p>
      </div>
    </div>
    <NotificationPanel
      @newNotifications="updateNewNotifications"
      @requestAnswered="decrementCounter"
      v-show="displayNotifications"
      id="notification-panel"
    />
  </div>
</template>

<script>
import NavButton from "@/components/nav/NavButton";
import NotificationPanel from "@/components/nav/NotificationPanel";
export default {
  name: "NavNotification",
  components: { NotificationPanel, NavButton },
  data() {
    return {
      displayNotifications: false,
      newNotifications: 0,
    };
  },
  methods: {
    openNotifications() {
      this.displayNotifications = true;
    },
    openNotificationsIfClosed() {
      if (!this.displayNotifications) {
        this.openNotifications();
      }
    },
    closeNotifications() {
      this.displayNotifications = false;
    },
    updateNewNotifications(value) {
      this.newNotifications = value;
    },
    // When a request is answered, it is removed and the count is decremented
    decrementCounter() {
      this.$store.dispatch("decrementRequestCounter");
      this.newNotifications = this.$store.state.rentalRequestsCount;
    },
  },
};
</script>

<style scoped>
#nav-np {
  display: flex;
  align-content: center;
}

#notification-button-container {
  display: flex;
  justify-items: center;
  align-items: center;
}

#notification-dot {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  border-radius: 100%;
  background-color: var(--red);
  margin-bottom: 13px;
  margin-left: 20px;
  width: 18px;
  aspect-ratio: 1/1;
}

#notification-dot > p {
  color: var(--white);
  font-size: 13px;
  margin: 0 1px 0 0;
}
</style>
