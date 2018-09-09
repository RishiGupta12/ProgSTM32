include $(top_srcdir)/flashstm32.mk

uart_jar="flashstm32uart.jar"
extlib_spcore="sp-core.jar"
extlib_sptty="sp-tty.jar"

.PHONY: uartsdk

all: uartsdk

uartsdk:
	$(call pry,"Building UART SDK ..............")
	ant clean
	ant -Duartjarname=$(uart_jar) uartsdk
	cp $(top_srcdir)/sdk/$(uart_jar) $(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_spcore) $(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_sptty) $(top_srcdir)/build
	rm $(top_srcdir)/sdk/$(uart_jar)

clean:
	ant -Duartjarname=$(uart_jar) clean
