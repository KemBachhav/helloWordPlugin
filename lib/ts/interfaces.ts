interface HelloWorldPlugin {

  /** Get the device's Universally Unique Identifier (UUID). */
  uuid: string;
  /** Get the operating system version. */
}

declare var helloWorldPlugin: HelloWorldPlugin;