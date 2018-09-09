include $(top_srcdir)/flashstm32.mk

.PHONY: uartsdk

all: uartsdk

uartsdk:
	$(call pry,"Building UART SDK ..............")
	ant clean
	ant uartsdk
	cp $(top_srcdir)/sdk/flashstm32uartsdk.jar $(top_srcdir)/build
	cp $(top_srcdir)/sdk/sp-core.jar $(top_srcdir)/build
	cp $(top_srcdir)/sdk/sp-tty.jar $(top_srcdir)/build
	rm $(top_srcdir)/sdk/flashstm32uartsdk.jar

clean:
	ant clean
