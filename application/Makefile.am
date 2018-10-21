include $(top_srcdir)/progstm32.mk

app_jarname=progstm32app.jar

extlib_uartsdk=progstm32uart.jar
extlib_spcore=sp-core.jar
extlib_sptty=sp-tty.jar

.PHONY: app

all: app

# Build java application
app:
	$(call pry,"Building application ..............")
	ant clean
	ant app -Dspcore=$(extlib_spcore) -Dsptty=$(extlib_sptty) -Duartsdk=$(extlib_uartsdk) -Dappjarname=$(app_jarname) -Dextlibpath=$(top_srcdir)/build
	cp $(top_srcdir)/application/$(app_jarname) $(top_srcdir)/build
	rm $(top_srcdir)/application/$(app_jarname)

clean:
	ant clean
