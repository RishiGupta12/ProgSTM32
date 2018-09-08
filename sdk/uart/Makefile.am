include $(top_srcdir)/flashstm32.mk

.PHONY: uartsdk

all: uartsdk

uartsdk:
	$(call pry,"Building UART SDK ..............")
	ant clean
	ant compile
	ant compress
	cp $(top_srcdir)/sdk/uart/stm32uartsdk.jar $(top_srcdir)/build
	cp $(top_srcdir)/sdk/uart/sp-core.jar $(top_srcdir)/build
	cp $(top_srcdir)/sdk/uart/sp-tty.jar $(top_srcdir)/build
	rm $(top_srcdir)/sdk/uart/stm32uartsdk.jar

clean:
	ant clean
