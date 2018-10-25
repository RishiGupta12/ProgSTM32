include $(top_srcdir)/progstm32.mk

.PHONY: app

all: app

# Build java application
app:
	$(call pry,"Building application ..............")
	ant clean
	ant app -Dspcore=$(extlib_spcore) -Dsptty=$(extlib_sptty) -Duartsdk=$(progstm32uart_jar) -Dappjarname=$(progstm32app_jar) -Dextlibpath=$(top_srcdir)/build

clean-local:
	ant clean
