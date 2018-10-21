include $(top_srcdir)/progstm32.mk

uart_jar=progstm32uart.jar
extlib_spcore=sp-core.jar
extlib_sptty=sp-tty.jar

.PHONY: uartsdk

all: uartsdk

dbgfile = core/flash/stm32/core/internal/Debug.java

# If D=1 passed set debug variable to true in java src file (dbgfile)
define check_dbg
	( if [ "x$(D)" == "x1" ]; \
	  then \
		sed -i -e '/static/s/false/true/' $(dbgfile); \
	  else \
		sed -i -e '/static/s/true/false/' $(dbgfile); \
	  fi )
endef

# Build UART SDK (generates, class and jar files)
uartsdk:
	$(call pry,"Building UART SDK ..............")
	ant clean
	$(call check_dbg)
	ant uartsdk -Duartjarname=$(uart_jar) -Dextlibpath=$(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_spcore) $(top_srcdir)/build
	cp $(top_srcdir)/sdk/$(extlib_sptty) $(top_srcdir)/build

javadoc: uartsdk
	ant doc -Ddocdir=$(top_srcdir)/documentation/uartsdk-javadocs

clean:
	ant -Duartjarname=$(uart_jar) clean
