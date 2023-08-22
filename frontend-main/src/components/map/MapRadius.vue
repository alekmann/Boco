<template>
  <div id="map-background-radius">
    <div id="map-container-radius">
      <div
        id="map"
        :style="'width:' + this.width + ';height:' + this.height"
      ></div>
      <div v-show="myLatLng">
        <div>
          <div id="map-text">Radius: {{ this.radius }} m</div>
          <div id="remove-button" @click="$emit('click', $event.target.value)">
            Fjern posisjon
          </div>
        </div>
        <input
          type="range"
          min="1"
          max="10000"
          id="radius"
          v-model="this.radius"
          @change="$emit('input', $event.target.value)"
          class="slider"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";

export default {
  props: ["showPositionOnly"],
  data() {
    return {
      locationObject: null,
      height: "300px",
      width: "300px",
      radius: 1000,
      myLatLng: null,
    };
  },
  methods: {
    toggleMap() {
      this.$emit("hideMap", this.locationObject);
    },
    resetMap() {
      if (this.circle && this.myLatLng) {
        this.circle.setMap(null);
        this.myLatLng = null;
      }
    },
  },
  async created() {
    const loader = new Loader({
      apiKey: "AIzaSyDge7nkZbCSQ57dNs6xgDW0is1-XYVhzOI",
      libraries: ["places"],
    });
    const mapOptions = {
      center: {
        lat: 63.429362,
        lng: 10.394357,
      },
      zoom: 12,
    };
    let google = await loader.load();
    var map = new google.maps.Map(document.getElementById("map"), mapOptions);
    // use this to access this instance inside google maps
    const that = this;

    // RADIUS INPUT FIELD
    document.getElementById("remove-button").addEventListener("click", () => {
      that.circle.setMap(null);
      that.myLatLng = null;

      that.$emit("changeMapLocation", null);
    });

    // RADIUS INPUT FIELD
    document.getElementById("radius").addEventListener("input", () => {
      if (that.circle != null) that.circle.setMap(null);

      that.circle = new google.maps.Circle({
        strokeColor: "#383636",
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: "#1fa9e0",
        fillOpacity: 0.35,
        map: map,
        center: that.myLatLng,
        radius: parseInt(that.radius),
      });

      that.$emit("changeMapLocation", {
        lat: that.myLatLng.lat,
        lng: that.myLatLng.lng,
        radius: that.radius,
      });
    });

    // CLICK MAP
    map.addListener("click", (event) => {
      that.myLatLng = {
        lat: event.latLng.lat(),
        lng: event.latLng.lng(),
      };

      if (that.circle != null) that.circle.setMap(null);

      that.circle = new google.maps.Circle({
        strokeColor: "#383636",
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: "#1fa9e0",
        fillOpacity: 0.35,
        map: map,
        center: that.myLatLng,
        radius: parseInt(that.radius),
      });

      // emit to parent
      that.$emit("changeMapLocation", {
        lat: that.myLatLng.lat,
        lng: that.myLatLng.lng,
        radius: that.radius,
      });
    });
  },
};
</script>

<style>
#map-background-radius {
  background-color: white;
  border-radius: 10px;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto auto auto;
  z-index: 12;
}

#map-container-radius {
  grid-row: 1/2;
  justify-self: center;
  background-color: white;
}

#map {
  overflow: hidden;
  border-radius: var(--standard-border-radius);
  margin-bottom: 10px;
}

#remove-button {
  float: right;
  background-color: var(--calm-red);
  padding: 3px 5px;
  cursor: pointer;
  font-size: 14px;
  align-self: center;
  color: var(--white);
  border-radius: var(--standard-border-radius);
}

#map-text {
  float: left;
  align-self: center;
}
</style>
