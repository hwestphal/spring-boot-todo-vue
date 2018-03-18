<template>
    <input type="text" :value="value" @input="input($event.target.value)" @keyup.enter="enter">
</template>

<script lang="ts">
export { default } from "./autocomplete";
</script>

<style src="awesomplete/awesomplete.css"></style>
