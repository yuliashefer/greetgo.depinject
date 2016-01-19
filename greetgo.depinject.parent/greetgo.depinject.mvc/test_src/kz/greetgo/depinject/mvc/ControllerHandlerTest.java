package kz.greetgo.depinject.mvc;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ControllerHandlerTest {

  private static final long RETURN_JSON = RND.plusLong(1000000000);
  private static final long RETURN_XML = RND.plusLong(1000000000);
  private static final String STR_PARAM_VALUE = RND.str(10);
  private static final String RETURN_DEFAULT_STR = RND.str(10);
  private static final String MODEL_PARAMETER_NAME = RND.str(10);
  private static final String MODEL_PARAMETER_VALUE = RND.str(10);

  @Mapping("/test")
  public static class TestController {

    public String strParam;

    @ToJson
    @Mapping("/to_json")
    public Object performToJson(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_JSON;
    }

    @ToXml
    @Mapping("/to_xml")
    public Object performToXml(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_XML;
    }

    @Mapping("/default_str")
    public String performDefaultStr(@Par("strParam") String strParam, MvcModel model) {
      this.strParam = strParam;

      model.setParam(MODEL_PARAMETER_NAME, MODEL_PARAMETER_VALUE);

      return RETURN_DEFAULT_STR;
    }

  }

  @Test
  public void create_handleTunnel_notHandled() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final ControllerHandler handler = ControllerHandler.create(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "b34h25b34h542hb42k5bh425hkb";

    //
    //
    final boolean handled = handler.handleTunnel(tunnel);
    //
    //

    assertThat(handled).isFalse();

  }

  @Test
  public void create_handleTunnel_to_json() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final ControllerHandler handler = ControllerHandler.create(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/to_json";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handler.handleTunnel(tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(tunnel.responseCharText()).isEqualTo("JSON " + RETURN_JSON);
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);

  }

  @Test
  public void create_handleTunnel_to_xml() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final ControllerHandler handler = ControllerHandler.create(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/to_xml";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handler.handleTunnel(tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(tunnel.responseCharText()).isEqualTo("XML " + RETURN_XML);
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);

  }

  @Test
  public void create_handleTunnel_default_str() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final ControllerHandler handler = ControllerHandler.create(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/default_str";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handler.handleTunnel(tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);
    assertThat(tunnel.responseBinText()).isEqualTo("view of " + RETURN_DEFAULT_STR);
    assertThat(views.returnValue).isEqualTo(RETURN_DEFAULT_STR);
    assertThat(views.model).isNotNull();
    assertThat(views.model.getParam(MODEL_PARAMETER_NAME)).isEqualTo(MODEL_PARAMETER_VALUE);
  }

}
