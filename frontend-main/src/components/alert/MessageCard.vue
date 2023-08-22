<template>
  <div id="mc-bg">
    <div id="card" class="animated fadeIn">
      <div id="upper-side">
        <i class="fa fa-check"></i>
        <h3 id="status">Suksess</h3>
      </div>
      <div id="lower-side">
        <p id="message">
          Din konto har blitt opprettet. Sjekk din epost for aktivering.
        </p>
        <button id="continue-button" @click="handleClick">Til Logg inn</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MessageCard.vue",
  methods: {
    handleClick() {
      this.$router.push({ name: "SignIn" });
    },
  },
};
</script>

<style scoped>
#mc-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgb(0, 0, 0, 0.8);
  z-index: 200;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
}

#card {
  position: relative;
  width: 320px;
  height: fit-content;
  display: block;
  text-align: center;
  font-family: "Source Sans Pro", sans-serif;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  border-radius: var(--standard-border-radius);
  margin-bottom: 100px;
}
#upper-side {
  padding: 2em;
  background-color: #f7a81b;
  display: block;
  color: #fff;
  border-top-right-radius: 8px;
  border-top-left-radius: 8px;
}
#checkmark {
  font-weight: lighter;
  fill: #fff;
  margin: -3.5em auto auto 20px;
}
#status {
  font-weight: lighter;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-size: 1em;
  margin-top: -0.2em;
  margin-bottom: 0;
}
#lower-side {
  padding: 2em 2em 5em 2em;
  background: #fff;
  display: block;
  border-bottom-right-radius: 8px;
  border-bottom-left-radius: 8px;
}
#message {
  margin-top: -0.5em;
  color: #757575;
  letter-spacing: 1px;
}
#continue-button {
  position: relative;
  top: 1.5em;
  text-decoration: none;
  background: #f7a81b;
  color: #fff;
  margin: auto;
  padding: 0.8em 3em;
  border-radius: 10px;
  transition: all 0.4s ease;
}
</style>
