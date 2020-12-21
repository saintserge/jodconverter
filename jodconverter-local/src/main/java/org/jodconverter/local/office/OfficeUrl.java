/*
 * Copyright 2004 - 2012 Mirko Nasato and contributors
 *           2016 - 2020 Simon Braconnier and contributors
 *
 * This file is part of JODConverter - Java OpenDocument Converter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jodconverter.local.office;

import java.util.Map;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.sun.star.lib.uno.helper.UnoUrl;

/**
 * Wrapper class around an UnoUrl so we are not importing the com.sun.star.lib.uno.helper.UnoUrl
 * package everywhere. UnoUrl are used to deal with UNO Interprocess Connection type and parameters.
 *
 * <p>OpenOffice.org supports two connection types: TCP sockets and named pipes. Named pipes are
 * marginally faster and do not take up a TCP port, but they require native libraries, which means
 * setting <em>java.library.path</em> when starting Java. E.g. on Linux
 *
 * <pre>
 * java -Djava.library.path=/opt/openoffice.org/ure/lib ...
 * </pre>
 *
 * <p>See <a
 * href="https://wiki.openoffice.org/wiki/Documentation/DevGuide/ProUNO/Opening_a_Connection">Opening
 * a Connection</a> and <a href="http://www.openoffice.org/udk/common/man/spec/uno-url.html">UNO Url
 * - Specification</a> in the OpenOffice.org Developer's Guide for more details.
 */
class OfficeUrl {

  private static final String DEFAULT_HOST = "127.0.0.1";
  private final UnoUrl unoUrl;

  /**
   * Creates an UnoUrl for the specified pipe.
   *
   * @param pipeName The pipe name.
   * @return The created UnoUrl.
   */
  /* default */
  static UnoUrl pipe(@NonNull String pipeName) {

    // Here we must use a try catch since OpenOffice and LibreOffice doesn't
    // have the same UnoUrl.parseUnoUrl signature
    try {
      return UnoUrl.parseUnoUrl("pipe,name=" + pipeName + ";urp;StarOffice.ServiceManager");
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Creates an UnoUrl for the specified port on host 127.0.0.1.
   *
   * @param port The port.
   * @return The created UnoUrl.
   */
  /* default */
  static UnoUrl socket(int port) {
    return socket(null, port);
  }

  /**
   * Creates an UnoUrl for the specified port.
   *
   * @param host The host. Uses 127.0.0.1 if null
   * @param port The port.
   * @return The created UnoUrl.
   */
  /* default */
  static UnoUrl socket(String host, int port) {

    String h = host == null ? DEFAULT_HOST : host;
    // Here we must use a try catch since OpenOffice and LibreOffice doesn't
    // have the same UnoUrl.parseUnoUrl signature
    try {
      return UnoUrl.parseUnoUrl(
          "socket,host=" + h + ",port=" + port + ",tcpNoDelay=1;urp;StarOffice.ServiceManager");
      //      return UnoUrl.parseUnoUrl(
      //          "socket,host=localhost,port=" + port + ";urp;StarOffice.ServiceManager");
    } catch (Exception ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Creates an OfficeUrl for the specified pipe.
   *
   * @param pipeName The pipe name.
   */
  public OfficeUrl(String pipeName) {

    unoUrl = pipe(pipeName);
  }

  /**
   * Creates an OfficeUrl for the specified port on host 127.0.0.1
   *
   * @param port The port.
   */
  public OfficeUrl(int port) {
    this(DEFAULT_HOST, port);
  }

  /**
   * Creates an OfficeUrl for the specified port.
   *
   * @param host The host.
   * @param port The port.
   */
  public OfficeUrl(String host, int port) {
    unoUrl = socket(host, port);
  }

  /**
   * Returns the name of the connection of this Uno Url. Encoded characters are not allowed.
   *
   * @return The connection name as string.
   */
  public String getConnection() {
    return unoUrl.getConnection();
  }

  /**
   * Returns the name of the protocol of this Uno Url. Encoded characters are not allowed.
   *
   * @return The protocol name as string.
   */
  public String getProtocol() {
    return unoUrl.getProtocol();
  }

  /**
   * Return the object name. Encoded character are not allowed.
   *
   * @return The object name as String.
   */
  public String getRootOid() {
    return unoUrl.getRootOid();
  }

  /**
   * Returns the protocol parameters as a map with key/value pairs. Encoded characters like '%41'
   * are decoded.
   *
   * @return A map with key/value pairs for protocol parameters.
   */
  public Map<String, String> getProtocolParameters() {
    return ((Map<?, ?>) unoUrl.getProtocolParameters())
        .entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
  }

  /**
   * Returns the connection parameters as a map with key/value pairs. Encoded characters like '%41'
   * are decoded.
   *
   * @return A map with key/value pairs for connection parameters.
   */
  public Map<String, String> getConnectionParameters() {
    return ((Map<?, ?>) unoUrl.getConnectionParameters())
        .entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
  }

  /**
   * Returns the raw specification of the protocol parameters. Encoded characters like '%41' are not
   * decoded.
   *
   * @return The uninterpreted protocol parameters as string.
   */
  public String getProtocolParametersAsString() {
    return unoUrl.getProtocolParametersAsString();
  }

  /**
   * Returns the raw specification of the connection parameters. Encoded characters like '%41' are
   * not decoded.
   *
   * @return The uninterpreted connection parameters as string.
   */
  public String getConnectionParametersAsString() {
    return unoUrl.getConnectionParametersAsString();
  }

  /**
   * Returns the raw specification of the protocol name and parameters. Encoded characters like
   * '%41' are not decoded.
   *
   * @return The uninterpreted protocol name and parameters as string.
   */
  public String getProtocolAndParametersAsString() {
    return unoUrl.getProtocolAndParametersAsString();
  }

  /**
   * Returns the raw specification of the connection name and parameters. Encoded characters like
   * '%41' are not decoded.
   *
   * @return The uninterpreted connection name and parameters as string.
   */
  public String getConnectionAndParametersAsString() {
    return unoUrl.getConnectionAndParametersAsString();
  }

  @Override
  public String toString() {
    return unoUrl.toString();
  }

  //  /**
  //   * Main entry point of the program used to test this class.
  //   *
  //   * @param args program arguments.
  //   */
  //  public static void main(final String[] args) {
  //
  //    // Here we must use a try catch since OpenOffice and LibreOffice doesn't
  //    // have the same UnoUrl.parseUnoUrl signature
  //    try {
  //      OfficeUrl url = new OfficeUrl(2002);
  //
  //      System.out.println("WITH PORT");
  //      System.out.println(String.format("url.getConnection(): %s", url.getConnection()));
  //      System.out.println(
  //          String.format(
  //              "url.getConnectionAndParametersAsString(): %s",
  //              url.getConnectionAndParametersAsString()));
  //      System.out.println(
  //          String.format(
  //              "url.getConnectionParametersAsString(): %s",
  // url.getConnectionParametersAsString()));
  //      System.out.println(
  //          String.format("url.getConnectionParameters(): %s", url.getConnectionParameters()));
  //      System.out.println(String.format("url.getProtocol(): %s", url.getProtocol()));
  //      System.out.println(
  //          String.format(
  //              "url.getProtocolAndParametersAsString(): %s",
  //              url.getProtocolAndParametersAsString()));
  //      System.out.println(
  //          String.format(
  //              "url.getProtocolParametersAsString(): %s", url.getProtocolParametersAsString()));
  //      System.out.println(
  //          String.format("url.getProtocolParameters(): %s", url.getProtocolParameters()));
  //      System.out.println(String.format("url.getRootOid(): %s", url.getRootOid()));
  //
  //      System.out.println();
  //      System.out.println();
  //
  //      url = new OfficeUrl("office");
  //
  //      System.out.println("WITH PIPE");
  //      System.out.println(String.format("url.getConnection(): %s", url.getConnection()));
  //      System.out.println(
  //          String.format(
  //              "url.getConnectionAndParametersAsString(): %s",
  //              url.getConnectionAndParametersAsString()));
  //      System.out.println(
  //          String.format(
  //              "url.getConnectionParametersAsString(): %s",
  // url.getConnectionParametersAsString()));
  //      System.out.println(
  //          String.format("url.getConnectionParameters(): %s", url.getConnectionParameters()));
  //      System.out.println(String.format("url.getProtocol(): %s", url.getProtocol()));
  //      System.out.println(
  //          String.format(
  //              "url.getProtocolAndParametersAsString(): %s",
  //              url.getProtocolAndParametersAsString()));
  //      System.out.println(
  //          String.format(
  //              "url.getProtocolParametersAsString(): %s", url.getProtocolParametersAsString()));
  //      System.out.println(
  //          String.format("url.getProtocolParameters(): %s", url.getProtocolParameters()));
  //      System.out.println(String.format("url.getRootOid(): %s", url.getRootOid()));
  //    } catch (Exception ex) {
  //      throw new IllegalArgumentException(ex);
  //    }
  //  }
}
