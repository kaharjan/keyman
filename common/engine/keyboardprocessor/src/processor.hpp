/*
  Copyright:    © 2018 SIL International.
  Description:  Internal keyboard class and adaptor class for the API.
  Create Date:  2 Oct 2018
  Authors:      Tim Eves (TSE)
  History:      2 Oct 2018 - TSE - Refactored out of km_kbp_keyboard_api.cpp
*/

#pragma once

#include <string>
#include <keyman/keyboardprocessor.h>
#include <kmx/kmx_processor.h>

namespace km {
namespace kbp
{

  class abstract_processor
  {
  private:
    km_kbp_keyboard_attrs const & _kb;
  protected:
    km_kbp_keyboard_attrs const &  keyboard() const noexcept { return _kb; }
  public:
    abstract_processor(km_kbp_keyboard_attrs const & kb) : _kb(kb)
	{}


    virtual km_kbp_status process_event(km_kbp_state *state, km_kbp_virtual_key vk, uint16_t modifier_state) = 0;
    virtual km_kbp_attr const * get_attrs() const = 0;
    virtual km_kbp_status validate() const = 0;
  };

  class kmx_processor : public abstract_processor
  {
  private:
    bool m_valid;
    KMX_Processor kmx;
    void load_keyboard();
  public:
    kmx_processor(km_kbp_keyboard_attrs const & kb);
    km_kbp_status process_event(km_kbp_state *state, km_kbp_virtual_key vk, uint16_t modifier_state);
    km_kbp_attr const * get_attrs() const;
    km_kbp_status validate() const;
  };

  class mock_processor : public abstract_processor
  {
  public:
    mock_processor(km_kbp_keyboard_attrs const & kb) : abstract_processor(kb) {
    }
    km_kbp_status process_event(km_kbp_state *state, km_kbp_virtual_key vk, uint16_t modifier_state);
    km_kbp_attr const * get_attrs() const;
    km_kbp_status validate() const;
  };

  class null_processor : public mock_processor {
  public:
    null_processor(km_kbp_keyboard_attrs const & kb) : mock_processor(kb) {
    }
    km_kbp_status validate() const;
  };

} // namespace kbp
} // namespace km
