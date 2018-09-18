include $(top_srcdir)/flashstm32.mk

uart_jar="flashstm32uart.jar"
extlib_spcore="sp-core.jar"
extlib_sptty="sp-tty.jar"

.PHONY: uartsdk

dbgfile = core/flash/stm32/core/internal/Debug.java

define check_dbg
	( if [ "x$(D)" == "x1" ]; \
	  then \
		sed -i -e '/static/s/false/true/' $(dbgfile); \
	  else \
		sed -i -e '/static/s/true/false/' $(dbgfile); \
	  fi )
endef

uartsdk:
	$(call pry,"Building UART SDK ..............")
	ant clean
	$(call check_dbg)
	ant uartsdk -Duartjarname=$(uart_jar)
	cp $(top_srcdir)/sdk/$(uart_jar) $(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_spcore) $(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_sptty) $(top_srcdir)/build
	rm $(top_srcdir)/sdk/$(uart_jar)

all: uartsdk

clean:
	ant -Duartjarname=$(uart_jar) clean
